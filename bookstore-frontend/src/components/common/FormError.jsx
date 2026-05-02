export default function FormError({ message }) {
  if (!message) return null;
  return (
    <p style={{
      color: '#ef4444',
      fontSize: '12px',
      marginTop: '4px',
      display: 'flex',
      alignItems: 'center',
      gap: '4px'
    }}>
      ⚠ {message}
    </p>
  );
}