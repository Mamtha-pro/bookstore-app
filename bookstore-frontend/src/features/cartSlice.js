import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',
  initialState: { items: [], totalAmount: 0, cartId: null },
  reducers: {
    setCart: (state, action) => {
      const { items, totalAmount, cartId } = action.payload;
      state.items = items || [];
      state.totalAmount = totalAmount || 0;
      state.cartId = cartId;
    },
    clearCartState: (state) => {
      state.items = [];
      state.totalAmount = 0;
    },
  },
});

export const { setCart, clearCartState } = cartSlice.actions;
export default cartSlice.reducer;