import * as React from "react";
import {createContext, ReactNode, useState} from "react";

type SellerProductsContextType = {
    sellerProducts: Array<SellerProductInfo> | null;
    setSellerProducts: (products: Array<SellerProductInfo>) => void;
}

// Create the context
export const SellerProductsContext = createContext<SellerProductsContextType>({
    sellerProducts: null,
    setSellerProducts: () => {throw new Error("setSellerProduct function not implemented")}
});

// Define a Provider Component
export function SellerProductsProvider ({ children }: { children: ReactNode }) {
    const [sellerProducts, setSellerProducts] = useState<Array<SellerProductInfo> | null>(null);
    return (
        <SellerProductsContext.Provider value={{ sellerProducts, setSellerProducts }}>
            {children}
        </SellerProductsContext.Provider>
    );
}