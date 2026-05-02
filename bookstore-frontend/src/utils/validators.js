// ── Common validation rules ───────────────────────────────────────

export const validateEmail = (email) => {
  if (!email || !email.trim())
    return 'Email is required';
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email))
    return 'Enter a valid email address';
  return null;
};

export const validatePassword = (password) => {
  if (!password || !password.trim())
    return 'Password is required';
  if (password.length < 6)
    return 'Password must be at least 6 characters';
  return null;
};

export const validateName = (name) => {
  if (!name || !name.trim())
    return 'Name is required';
  if (name.trim().length < 2)
    return 'Name must be at least 2 characters';
  return null;
};

export const validateAddress = (address) => {
  if (!address || !address.trim())
    return 'Address is required';
  if (address.trim().length < 10)
    return 'Please enter a complete address';
  return null;
};

export const validatePrice = (price) => {
  if (!price && price !== 0)
    return 'Price is required';
  if (isNaN(price) || Number(price) <= 0)
    return 'Price must be a positive number';
  return null;
};

export const validateStock = (stock) => {
  if (!stock && stock !== 0)
    return 'Stock is required';
  if (isNaN(stock) || Number(stock) < 0)
    return 'Stock must be 0 or more';
  return null;
};

export const validateUpi = (upiId) => {
  if (!upiId || !upiId.trim())
    return 'UPI ID is required';
  if (!upiId.includes('@'))
    return 'Enter valid UPI ID (e.g. name@upi)';
  return null;
};

export const validateCard = (cardNumber, cardHolder, expiry, cvv) => {
  const errors = {};
  const raw = cardNumber?.replace(/\s/g, '') || '';
  if (raw.length < 16)
    errors.cardNumber = 'Enter a valid 16-digit card number';
  if (!cardHolder?.trim())
    errors.cardHolder = 'Cardholder name is required';
  if (!expiry || expiry.length < 5)
    errors.expiry = 'Enter valid expiry (MM/YY)';
  if (!cvv || cvv.length < 3)
    errors.cvv = 'Enter valid 3-digit CVV';
  return errors;
};

export const validateReview = (rating, comment) => {
  const errors = {};
  if (!rating || rating < 1 || rating > 5)
    errors.rating = 'Rating must be between 1 and 5';
  if (!comment || comment.trim().length < 5)
    errors.comment = 'Comment must be at least 5 characters';
  return errors;
};