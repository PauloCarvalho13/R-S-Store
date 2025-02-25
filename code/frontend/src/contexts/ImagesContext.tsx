import * as React from "react";
import {createContext, useState} from "react";

type ImagesContext = {
    images: Array<ImageDetails> | null;
    setImages: (images: Array<ImageDetails>) => void;
}

// Create the context
export const ImagesContext = createContext<ImagesContext>({
    images: null,
    setImages: () => {throw new Error("setProductImages function not implemented")}
});

// Define a Provider Component
export function ImagesProvider ({ children }: { children: React.ReactNode }) {
    const [images, setImages] = useState<Array<ImageDetails> | null>(null);
    return (
        <ImagesContext.Provider value={{ images, setImages}}>
            {children}
        </ImagesContext.Provider>
    );
}