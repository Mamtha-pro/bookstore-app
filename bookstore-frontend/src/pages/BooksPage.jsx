import { useState, useEffect } from 'react';
import { getAllBooks, searchBooks } from '../api/bookApi';
import BookCard from '../components/common/BookCard';
import { FiSearch } from 'react-icons/fi';

export default function BooksPage() {
  const [books, setBooks]     = useState([]);
  const [total, setTotal]     = useState(0);
  const [page, setPage]       = useState(0);
  const [query, setQuery]     = useState('');
  const [loading, setLoading] = useState(true);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = query
        ? await searchBooks(query, page)
        : await getAllBooks(page);
      setBooks(res.data.content);
      setTotal(res.data.totalPages);
    } catch {
      setBooks([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBooks(); }, [page, query]);

  return (
    <div className="page">
      <div style={{ marginBottom: 28 }}>
        <h1 style={{ fontSize: 28, fontWeight: 800, marginBottom: 16 }}>
          📚 All Books
        </h1>
        <div style={{ position: 'relative', maxWidth: 400 }}>
          <FiSearch style={{
            position: 'absolute', left: 12, top: '50%',
            transform: 'translateY(-50%)', color: '#9ca3af'
          }} />
          <input
            style={{
              width: '100%', padding: '10px 10px 10px 38px',
              border: '1.5px solid var(--border)',
              borderRadius: 8, fontSize: 14, outline: 'none'
            }}
            placeholder="Search by title or author..."
            value={query}
            onChange={(e) => { setQuery(e.target.value); setPage(0); }}
          />
        </div>
      </div>

      {loading
        ? <div className="spinner" />
        : books.length === 0
          ? <div className="empty-state"><h3>No books found</h3></div>
          : <div className="grid-4">{books.map(b => <BookCard key={b.id} book={b} />)}</div>
      }

      {total > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 30 }}>
          {Array.from({ length: total }, (_, i) => (
            <button
              key={i}
              onClick={() => setPage(i)}
              className={`btn btn-sm ${i === page ? 'btn-primary' : 'btn-outline'}`}
            >{i + 1}</button>
          ))}
        </div>
      )}
    </div>
  );
}