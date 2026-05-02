import { useEffect, useState } from 'react';
import { getAllBooks } from '../../api/bookApi';
import { createBook, updateBook, deleteBook } from '../../api/bookApi';
import toast from 'react-hot-toast';
import { FiEdit, FiTrash2, FiPlus } from 'react-icons/fi';

const empty = { title:'', author:'', isbn:'', price:'', category:'', stock:'', description:'', imageUrl:'' };

export default function AdminBooks() {
  const [books, setBooks]   = useState([]);
  const [modal, setModal]   = useState(false);
  const [form, setForm]     = useState(empty);
  const [editing, setEditing] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = () =>
    getAllBooks(0, 100).then(r => setBooks(r.data.content)).finally(() => setLoading(false));

  useEffect(() => { load(); }, []);

  const openAdd  = () => { setForm(empty); setEditing(null); setModal(true); };
  const openEdit = (b) => {
    setForm({ ...b, price: b.price.toString(), stock: b.stock.toString() });
    setEditing(b.id); setModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const payload = { ...form, price: parseFloat(form.price), stock: parseInt(form.stock) };
    try {
      editing ? await updateBook(editing, payload) : await createBook(payload);
      toast.success(editing ? 'Book updated' : 'Book added');
      setModal(false); load();
    } catch { toast.error('Save failed'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this book?')) return;
    await deleteBook(id); toast.success('Deleted'); load();
  };

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <div style={{ display:'flex', justifyContent:'space-between', marginBottom:20 }}>
        <h1 style={{ fontSize:24, fontWeight:800 }}>📚 Books</h1>
        <button className="btn btn-primary btn-sm" onClick={openAdd}>
          <FiPlus /> Add Book
        </button>
      </div>

      <div className="card table-wrap">
        <table>
          <thead>
            <tr><th>Title</th><th>Author</th><th>Category</th><th>Price</th><th>Stock</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {books.map(b => (
              <tr key={b.id}>
                <td style={{ fontWeight:600 }}>{b.title}</td>
                <td>{b.author}</td>
                <td><span className="badge badge-info">{b.category}</span></td>
                <td>₹{b.price}</td>
                <td><span className={`badge ${b.stock>0?'badge-success':'badge-danger'}`}>{b.stock}</span></td>
                <td style={{ whiteSpace: 'nowrap' }}>
                  <button className="btn btn-outline btn-sm" style={{ marginRight:6 }} onClick={() => openEdit(b)}>
                    <FiEdit />
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(b.id)}>
                    <FiTrash2 />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <div style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:999 }}>
          <div className="card" style={{ width:500, maxHeight:'90vh', overflowY:'auto' }}>
            <h2 style={{ marginBottom:20 }}>{editing ? 'Edit Book' : 'Add Book'}</h2>
            <form onSubmit={handleSave}>
              {['title','author','isbn','category','imageUrl'].map(f => (
                <div className="form-group" key={f}>
                  <label style={{ textTransform:'capitalize' }}>{f}</label>
                  <input value={form[f]} onChange={e => setForm({...form,[f]:e.target.value})}
                    required={['title','author'].includes(f)} />
                </div>
              ))}
              <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
                <div className="form-group">
                  <label>Price</label>
                  <input type="number" step="0.01" value={form.price}
                    onChange={e => setForm({...form,price:e.target.value})} required />
                </div>
                <div className="form-group">
                  <label>Stock</label>
                  <input type="number" value={form.stock}
                    onChange={e => setForm({...form,stock:e.target.value})} required />
                </div>
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea rows={3} value={form.description}
                  onChange={e => setForm({...form,description:e.target.value})} />
              </div>
              <div style={{ display:'flex', gap:10 }}>
                <button className="btn btn-primary" style={{ flex:1 }}>Save</button>
                <button type="button" className="btn btn-outline" style={{ flex:1 }}
                  onClick={() => setModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}