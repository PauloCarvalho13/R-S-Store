import * as React from "react";
import {createContext, useState} from "react";

type ProductsContextType = {
  products: Array<ProductOverview> | null;
  setProducts: (products: Array<ProductOverview>) => void;
}

// Create the context
export const ProductsContext = createContext<ProductsContextType>({
    products: null,
    setProducts: () => {throw new Error("setProduct function not implemented")}
});

// Define a Provider Component
export function ProductsProvider ({ children }: { children: React.ReactNode }) {
  const [products, setProducts] = useState<Array<ProductOverview> | null>(null);

  return (
    <ProductsContext.Provider value={{ products, setProducts }}>
      {children}
    </ProductsContext.Provider>
  );
}

