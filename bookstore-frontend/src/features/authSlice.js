import { createSlice } from '@reduxjs/toolkit';

const token = localStorage.getItem('token');
const user  = JSON.parse(localStorage.getItem('user') || 'null');

const authSlice = createSlice({
  name: 'auth',
  initialState: { token, user, isAuthenticated: !!token },
  reducers: {
    setCredentials: (state, action) => {
      const { token, role, email, name } = action.payload;
      state.token = token;
      state.user  = { email, name, role };
      state.isAuthenticated = true;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify({ email, name, role }));
    },
    logout: (state) => {
      state.token = null;
      state.user  = null;
      state.isAuthenticated = false;
      localStorage.clear();
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;