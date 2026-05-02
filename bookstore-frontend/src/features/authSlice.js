import { createSlice } from '@reduxjs/toolkit';

// Load from localStorage on page refresh
const token = localStorage.getItem('token');
const user  = JSON.parse(localStorage.getItem('user') || 'null');

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token,
    user,
    isAuthenticated: !!token,
  },
  reducers: {
    setCredentials: (state, action) => {
      const { token, role, email, name } = action.payload;

      state.token           = token;
      state.isAuthenticated = true;

      // ✅ Make sure role is saved correctly
      state.user = {
        email: email,
        name:  name  || email.split('@')[0],
        role:  role,   // must be "ADMIN" or "USER"
      };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(state.user));

      // Debug — remove after fixing
      console.log('Credentials saved:', state.user);
    },

    logout: (state) => {
      state.token           = null;
      state.user            = null;
      state.isAuthenticated = false;
      localStorage.clear();
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;