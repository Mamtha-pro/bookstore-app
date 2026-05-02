import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';
import { setCart, clearCartState } from '../../features/cartSlice';
import toast from 'react-hot-toast';
import {
  FiShield, FiSmartphone, FiCreditCard,
  FiGlobe, FiPackage, FiTruck, FiCheck
} from 'react-icons/fi';
import './CheckoutPage.css';

const METHODS = [
  { id:'UPI',         label:'UPI',
    icon:<FiSmartphone/>, desc:'GPay · PhonePe · Paytm · Any UPI' },
  { id:'CARD',        label:'Credit / Debit Card',
    icon:<FiCreditCard/>, desc:'Visa · Mastercard · RuPay · Amex'  },
  { id:'NET_BANKING', label:'Net Banking',
    icon:<FiGlobe/>,      desc:'All major Indian banks'             },
  { id:'WALLET',      label:'Wallet',
    icon:<FiPackage/>,    desc:'Paytm · Amazon Pay · Mobikwik'      },
  { id:'COD',         label:'Cash on Delivery',
    icon:<FiTruck/>,      desc:'Pay when your order arrives'        },
];

const BANKS = [
  'State Bank of India', 'HDFC Bank', 'ICICI Bank',
  'Axis Bank', 'Kotak Mahindra Bank', 'Punjab National Bank',
  'Bank of Baroda', 'Canara Bank', 'Union Bank of India',
  'IndusInd Bank',
];

const WALLETS = [
  'Paytm', 'Amazon Pay', 'PhonePe Wallet',
  'Mobikwik', 'Freecharge', 'Airtel Money',
];

export default function CheckoutPage() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useSelector(s => s.auth);

  // ── Local cart state — loaded fresh from backend ──────────────────
  const [cartItems,    setCartItems]   = useState([]);
  const [totalAmount,  setTotalAmount] = useState(0);
  const [cartLoading,  setCartLoading] = useState(true);

  const [address,    setAddress]    = useState('');
  const [method,     setMethod]     = useState('UPI');
  const [step,       setStep]       = useState(1);
  const [loading,    setLoading]    = useState(false);
  const [processing, setProcessing] = useState(false);
  const [success,    setSuccess]    = useState(false);
  const [txnId,      setTxnId]      = useState('');

  // UPI
  const [upiId, setUpiId] = useState('');

  // Card
  const [cardNumber,  setCardNumber]  = useState('');
  const [cardHolder,  setCardHolder]  = useState('');
  const [expiry,      setExpiry]      = useState('');
  const [cvv,         setCvv]         = useState('');
  const [cardFlipped, setCardFlipped] = useState(false);

  // Bank / Wallet
  const [bankName,   setBankName]   = useState(BANKS[0]);
  const [walletName, setWalletName] = useState(WALLETS[0]);

  // ── Load cart from backend on mount ──────────────────────────────
  useEffect(() => {
    const loadCart = async () => {
      try {
        setCartLoading(true);
        const res  = await api.get('/api/cart');
        const data = res.data.data;
        dispatch(setCart(data));
        setCartItems(data.items || []);
        setTotalAmount(data.totalAmount || 0);
      } catch (err) {
        toast.error('Failed to load cart');
      } finally {
        setCartLoading(false);
      }
    };
    loadCart();
  }, [dispatch]);

  // ── Format helpers ────────────────────────────────────────────────
  const formatCard = (val) => {
    const num = val.replace(/\D/g, '').substring(0, 16);
    return num.replace(/(.{4})/g, '$1 ').trim();
  };

  const formatExpiry = (val) => {
    const num = val.replace(/\D/g, '').substring(0, 4);
    if (num.length >= 2)
      return num.substring(0, 2) + '/' + num.substring(2);
    return num;
  };

  // ── Step 1 → Step 2 ───────────────────────────────────────────────
  const goToPayment = () => {
    if (!address.trim()) {
      toast.error('Please enter delivery address');
      return;
    }
    setStep(2);
  };

  // ── Pay Now ───────────────────────────────────────────────────────
  const handlePay = async () => {
    if (!cartItems || cartItems.length === 0) {
      toast.error('Cart is empty! Add books first.');
      navigate('/books');
      return;
    }
    if (!address.trim()) {
      toast.error('Please enter delivery address');
      return;
    }
    if (method === 'UPI' && !upiId.includes('@')) {
      toast.error('Enter valid UPI ID (e.g. name@upi)');
      return;
    }
    if (method === 'CARD') {
      const rawCard = cardNumber.replace(/\s/g, '');
      if (rawCard.length < 16) { toast.error('Enter valid 16-digit card number'); return; }
      if (!cardHolder.trim())  { toast.error('Enter cardholder name');            return; }
      if (expiry.length < 5)   { toast.error('Enter valid expiry (MM/YY)');       return; }
      if (cvv.length < 3)      { toast.error('Enter valid CVV');                  return; }
    }

    setLoading(true);
    setProcessing(true);

    try {
      const res   = await api.post('/api/orders', { address: address.trim() });
      const order = res.data.data;

      await new Promise(resolve => setTimeout(resolve, 2000));

      const paymentData = {
        orderId:       order.id,
        paymentMethod: method,
        upiId:         method === 'UPI'         ? upiId                        : null,
        cardNumber:    method === 'CARD'        ? cardNumber.replace(/\s/g,'') : null,
        cardHolder:    method === 'CARD'        ? cardHolder                   : null,
        expiry:        method === 'CARD'        ? expiry                       : null,
        cvv:           method === 'CARD'        ? cvv                          : null,
        bankName:      method === 'NET_BANKING' ? bankName                     : null,
        walletName:    method === 'WALLET'      ? walletName                   : null,
      };

      const payRes  = await api.post('/api/payments/initiate', paymentData);
      const payment = payRes.data.data;

      setTxnId(payment.transactionId);
      setSuccess(true);
      setProcessing(false);
      dispatch(clearCartState());

    } catch (err) {
      setProcessing(false);
      setLoading(false);
      const errMsg = err.response?.data?.message || 'Payment failed. Please try again.';
      toast.error(errMsg);
    }
  };

  // ── Loading screen ────────────────────────────────────────────────
  if (cartLoading) {
    return (
      <div className="page">
        <div className="loading-center">
          <div className="spinner" />
          <p>Loading your cart...</p>
        </div>
      </div>
    );
  }

  // ── Empty Cart screen ─────────────────────────────────────────────
  if (!cartLoading && cartItems.length === 0) {
    return (
      <div className="page">
        <div className="empty-state">
          <div style={{ fontSize: 60, marginBottom: 16 }}>🛒</div>
          <h3>Your cart is empty!</h3>
          <p>Add some books before checkout</p>
          <button
            className="btn btn-primary"
            style={{ marginTop: 16 }}
            onClick={() => navigate('/books')}>
            Browse Books
          </button>
        </div>
      </div>
    );
  }

  // ── Processing screen ─────────────────────────────────────────────
  if (processing) {
    return (
      <div className="page">
        <div className="payment-processing">
          <div className="processing-spinner" />
          <h2>Processing Payment...</h2>
          <p>Please wait, do not close this page</p>
          <div className="processing-steps">
            <div className="proc-step done">
              <FiCheck /> Connecting to bank
            </div>
            <div className="proc-step active">
              <div className="mini-spin" /> Verifying details
            </div>
            <div className="proc-step">
              <span>○</span> Confirming payment
            </div>
          </div>
        </div>
      </div>
    );
  }

  // ── Success screen ────────────────────────────────────────────────
  if (success) {
    return (
      <div className="page">
        <div className="payment-success">
          <div className="success-icon">
            <FiCheck size={48} color="white" />
          </div>
          <h1>Payment Successful!</h1>
          <p className="success-amount">₹{totalAmount}</p>
          <div className="success-txn">
            <span>Transaction ID</span>
            <code>{txnId}</code>
          </div>
          <div className="success-details">
            <div className="success-row">
              <span>Payment Method</span>
              <span>{method}</span>
            </div>
            <div className="success-row">
              <span>Status</span>
              <span className="badge badge-success">CONFIRMED ✅</span>
            </div>
          </div>
          <button
            className="btn btn-primary btn-full"
            style={{ marginTop: 24 }}
            onClick={() => navigate('/orders')}>
            View My Orders →
          </button>
          <button
            className="btn btn-outline btn-full"
            style={{ marginTop: 10 }}
            onClick={() => navigate('/books')}>
            Continue Shopping
          </button>
        </div>
      </div>
    );
  }

  // ── Main Checkout UI ──────────────────────────────────────────────
  return (
    <div className="checkout-page page">
      <h1 className="checkout-title">🛒 Checkout</h1>

      <div className="checkout-grid">

        {/* ── Left Column ──────────────────────────── */}
        <div>

          {/* Step 1: Address */}
          <div className={`co-card ${step >= 1 ? 'co-active' : ''}`}>
            <div className="co-step-head">
              <div className="co-step-num">1</div>
              <h2>Delivery Address</h2>
              {step > 1 && (
                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => setStep(1)}>
                  Edit
                </button>
              )}
            </div>

            {step === 1 ? (
              <>
                <textarea
                  className="co-textarea"
                  rows={4}
                  value={address}
                  onChange={e => setAddress(e.target.value)}
                  placeholder="House no, Street, Area, City, State, PIN code"
                />
                <button
                  className="btn btn-primary btn-full"
                  onClick={goToPayment}
                  disabled={!address.trim()}>
                  Continue to Payment →
                </button>
              </>
            ) : (
              <p className="co-address-preview">📍 {address}</p>
            )}
          </div>

          {/* Step 2: Payment */}
          {step === 2 && (
            <div className="co-card co-active">
              <div className="co-step-head">
                <div className="co-step-num">2</div>
                <h2>Payment Method</h2>
              </div>

              {/* Method selector */}
              <div className="co-methods">
                {METHODS.map(m => (
                  <button
                    key={m.id}
                    className={`co-method ${method === m.id ? 'co-selected' : ''}`}
                    onClick={() => setMethod(m.id)}>
                    <span className="co-method-icon">{m.icon}</span>
                    <div className="co-method-text">
                      <span className="co-method-label">{m.label}</span>
                      <span className="co-method-desc">{m.desc}</span>
                    </div>
                    <div className={`co-radio ${method === m.id ? 'co-radio-on' : ''}`} />
                  </button>
                ))}
              </div>

              {/* ── UPI ──────────────────────────────── */}
              {method === 'UPI' && (
                <div className="payment-form">
                  <h3>Enter UPI ID</h3>
                  <div className="form-group">
                    <label>UPI ID</label>
                    <input
                      value={upiId}
                      onChange={e => setUpiId(e.target.value)}
                      placeholder="yourname@upi"
                    />
                    <small>e.g. mobilenumber@upi, name@oksbi</small>
                  </div>
                  <div className="upi-apps">
                    <span>Quick fill:</span>
                    <button className="upi-app" onClick={() =>
                      setUpiId(user?.name?.toLowerCase()?.replace(' ','') + '@okaxis')}>
                      GPay
                    </button>
                    <button className="upi-app" onClick={() =>
                      setUpiId(user?.name?.toLowerCase()?.replace(' ','') + '@ybl')}>
                      PhonePe
                    </button>
                    <button className="upi-app" onClick={() =>
                      setUpiId(user?.name?.toLowerCase()?.replace(' ','') + '@paytm')}>
                      Paytm
                    </button>
                  </div>
                </div>
              )}

              {/* ── Card ─────────────────────────────── */}
              {method === 'CARD' && (
                <div className="payment-form">
                  <h3>Card Details</h3>

                  {/* Card Preview */}
                  <div className={`card-preview ${cardFlipped ? 'flipped' : ''}`}>
                    <div className="card-front">
                      <div className="card-chip">💳</div>
                      <div className="card-number-display">
                        {cardNumber || '•••• •••• •••• ••••'}
                      </div>
                      <div className="card-bottom">
                        <div>
                          <div className="card-label">Card Holder</div>
                          <div className="card-value">{cardHolder || 'YOUR NAME'}</div>
                        </div>
                        <div>
                          <div className="card-label">Expires</div>
                          <div className="card-value">{expiry || 'MM/YY'}</div>
                        </div>
                      </div>
                    </div>
                    <div className="card-back">
                      <div className="card-stripe" />
                      <div className="card-cvv-row">
                        <span>CVV</span>
                        <div className="card-cvv-box">
                          {cvv ? '•'.repeat(cvv.length) : '•••'}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Card Number</label>
                    <input
                      value={cardNumber}
                      onChange={e => setCardNumber(formatCard(e.target.value))}
                      placeholder="1234 5678 9012 3456"
                      maxLength={19}
                    />
                  </div>
                  <div className="form-group">
                    <label>Cardholder Name</label>
                    <input
                      value={cardHolder}
                      onChange={e => setCardHolder(e.target.value.toUpperCase())}
                      placeholder="NAME AS ON CARD"
                    />
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                    <div className="form-group">
                      <label>Expiry Date</label>
                      <input
                        value={expiry}
                        onChange={e => setExpiry(formatExpiry(e.target.value))}
                        placeholder="MM/YY"
                        maxLength={5}
                      />
                    </div>
                    <div className="form-group">
                      <label>CVV</label>
                      <input
                        value={cvv}
                        onChange={e => setCvv(e.target.value.replace(/\D/g,'').substring(0,3))}
                        placeholder="•••"
                        maxLength={3}
                        type="password"
                        onFocus={() => setCardFlipped(true)}
                        onBlur={()  => setCardFlipped(false)}
                      />
                    </div>
                  </div>
                  <small style={{ color: 'var(--muted)' }}>
                    🔒 Card details are encrypted and secure
                  </small>
                </div>
              )}

              {/* ── Net Banking ───────────────────────── */}
              {method === 'NET_BANKING' && (
                <div className="payment-form">
                  <h3>Select Your Bank</h3>
                  <div className="bank-grid">
                    {BANKS.map(bank => (
                      <button key={bank}
                        className={`bank-option ${bankName === bank ? 'bank-selected' : ''}`}
                        onClick={() => setBankName(bank)}>
                        🏦 {bank}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* ── Wallet ────────────────────────────── */}
              {method === 'WALLET' && (
                <div className="payment-form">
                  <h3>Select Wallet</h3>
                  <div className="wallet-grid">
                    {WALLETS.map(w => (
                      <button key={w}
                        className={`wallet-option ${walletName === w ? 'wallet-selected' : ''}`}
                        onClick={() => setWalletName(w)}>
                        👜 {w}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* ── COD ──────────────────────────────── */}
              {method === 'COD' && (
                <div className="payment-form">
                  <div className="cod-info">
                    <FiTruck size={40} color="var(--primary)" />
                    <h3>Cash on Delivery</h3>
                    <p>Pay ₹{totalAmount} when your order arrives.</p>
                    <ul>
                      <li>✅ No advance payment needed</li>
                      <li>✅ Pay in cash at your door</li>
                      <li>✅ Free cancellation before shipping</li>
                    </ul>
                  </div>
                </div>
              )}

              {/* Security badge */}
              <div className="co-secure">
                <FiShield color="#22c55e" size={16} />
                <span>100% Secure · 256-bit SSL Encrypted</span>
              </div>

              {/* Pay button */}
              <button
                className="btn btn-primary btn-full co-pay-btn"
                onClick={handlePay}
                disabled={loading}>
                {loading
                  ? '⏳ Processing...'
                  : method === 'COD'
                    ? 'Place Order (Cash on Delivery)'
                    : 'Pay ₹' + totalAmount + ' Now'}
              </button>
            </div>
          )}
        </div>

        {/* ── Right: Order Summary ──────────────────── */}
        <div className="co-card co-summary">
          <h2>Order Summary</h2>

          <div className="co-summary-items">
            {cartItems.map(item => (
              <div key={item.itemId} className="co-summary-row">
                <span>
                  {item.bookTitle}
                  <small> ×{item.quantity}</small>
                </span>
                <span>₹{item.subtotal}</span>
              </div>
            ))}
          </div>

          <hr className="co-divider" />

          <div className="co-summary-row">
            <span>Subtotal</span>
            <span>₹{totalAmount}</span>
          </div>
          <div className="co-summary-row">
            <span>Delivery</span>
            <span style={{ color: 'var(--success)', fontWeight: 700 }}>FREE</span>
          </div>

          <hr className="co-divider" />

          <div className="co-summary-row co-total">
            <span>Total</span>
            <span>₹{totalAmount}</span>
          </div>

          <div className="co-trust">
            <span> Free Returns</span>
            <span>Secure</span>
            <span> Fast Ship</span>
          </div>
        </div>

      </div>
    </div>
  );
}