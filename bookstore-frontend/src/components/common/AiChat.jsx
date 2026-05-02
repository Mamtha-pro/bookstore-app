import { useState, useRef, useEffect } from 'react';
import api from '../../api/axios';
import { useSelector } from 'react-redux';
import { FiSend, FiX, FiMessageCircle } from 'react-icons/fi';
import { BsRobot } from 'react-icons/bs';
import './AiChat.css';

// ── Quick suggestion chips ────────────────────────────────────────
const SUGGESTIONS = [
  '📚 Books under ₹500',
  '🎯 Recommend programming books',
  '📦 Show my orders',
  '⭐ Best fiction books',
  '🔍 Find java books',
  '💼 Business books',
];

export default function AiChat() {
  const { isAuthenticated, user } = useSelector(s => s.auth);

  const [open,     setOpen]     = useState(false);
  const [input,    setInput]    = useState('');
  const [loading,  setLoading]  = useState(false);
  const [messages, setMessages] = useState([
    {
      role: 'ai',
      text: '👋 Hi! I am your BookStore AI Assistant!\n\nI can help you:\n📚 Find books by price\n🎯 Get recommendations\n📦 Track your orders\n🔍 Search books\n\nWhat can I help you with today?',
    }
  ]);

  const bottomRef = useRef(null);

  // Auto scroll to bottom when new message arrives
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // ── Send Message ──────────────────────────────────────────────────
  const sendMessage = async (text) => {
    const msg = text || input.trim();
    if (!msg || loading) return;

    // Add user message
    setMessages(prev => [...prev, { role: 'user', text: msg }]);
    setInput('');
    setLoading(true);

    try {
      const res = await api.post('/api/ai/chat', {
        message:   msg,
        userEmail: isAuthenticated ? user?.email : null,
      });

      // Add AI reply
      setMessages(prev => [...prev, {
        role: 'ai',
        text: res.data.reply,
        type: res.data.type,
      }]);

    } catch (err) {
      setMessages(prev => [...prev, {
        role: 'ai',
        text: '😅 Sorry, something went wrong. Please try again!',
        type: 'error',
      }]);
    } finally {
      setLoading(false);
    }
  };

  // ── Enter key sends message ───────────────────────────────────────
  const handleKey = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  // ── Format message text with line breaks ──────────────────────────
  const formatText = (text) => {
    return text.split('\n').map((line, i) => (
      <p key={i} style={{ margin: '2px 0', lineHeight: 1.5 }}>
        {line || '\u00A0'}
      </p>
    ));
  };

  return (
    <>
      {/* ── Floating Button ─────────────────────────────────── */}
      <button
        className="ai-fab"
        onClick={() => setOpen(!open)}
        title="AI Assistant"
      >
        {open
          ? <FiX size={22} />
          : <>
              <BsRobot size={22} />
              <span className="ai-fab-text">AI Help</span>
            </>
        }
      </button>

      {/* ── Chat Window ─────────────────────────────────────── */}
      {open && (
        <div className="ai-window">

          {/* Header */}
          <div className="ai-header">
            <div className="ai-header-left">
              <div className="ai-avatar-sm">
                <BsRobot size={16} />
              </div>
              <div>
                <div className="ai-header-title">BookStore AI</div>
                <div className="ai-header-sub">
                  <span className="ai-dot" /> Always here to help
                </div>
              </div>
            </div>
            <button className="ai-close-btn" onClick={() => setOpen(false)}>
              <FiX size={18} />
            </button>
          </div>

          {/* Messages */}
          <div className="ai-messages">
            {messages.map((msg, i) => (
              <div key={i} className={`ai-msg-row ${msg.role}`}>
                {msg.role === 'ai' && (
                  <div className="ai-msg-avatar">
                    <BsRobot size={13} />
                  </div>
                )}
                <div className={`ai-bubble ${msg.role}`}>
                  {formatText(msg.text)}
                </div>
              </div>
            ))}

            {/* Typing indicator */}
            {loading && (
              <div className="ai-msg-row ai">
                <div className="ai-msg-avatar">
                  <BsRobot size={13} />
                </div>
                <div className="ai-bubble ai ai-typing">
                  <span /><span /><span />
                </div>
              </div>
            )}

            <div ref={bottomRef} />
          </div>

          {/* Suggestion chips — show only at start */}
          {messages.length <= 2 && (
            <div className="ai-suggestions">
              {SUGGESTIONS.map((s, i) => (
                <button
                  key={i}
                  className="ai-chip"
                  onClick={() => sendMessage(s)}
                  disabled={loading}
                >
                  {s}
                </button>
              ))}
            </div>
          )}

          {/* Input Row */}
          <div className="ai-input-row">
            <input
              className="ai-input"
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKey}
              placeholder="Ask me anything..."
              disabled={loading}
            />
            <button
              className="ai-send-btn"
              onClick={() => sendMessage()}
              disabled={loading || !input.trim()}
            >
              <FiSend size={16} />
            </button>
          </div>

        </div>
      )}
    </>
  );
}