export default function Footer() {
  return (
    <footer style={{
      background: '#1f2937',
      color: '#9ca3af',
      textAlign: 'center',
      padding: '20px',
      marginTop: '0',        // ✅ remove the gap
      fontSize: '14px',
      width: '100%',
      boxSizing: 'border-box'
    }}>
      © 2026 BookStore — Built with Spring Boot + React
    </footer>
  );
}