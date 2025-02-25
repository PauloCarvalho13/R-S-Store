import * as React from 'react';
import {useContext, useEffect, useReducer, useState} from "react";
import {AuthUserContext} from "../../contexts/AuthUserContext";
import {SellerProductsContext} from "../../contexts/SellerProductsContext";
import {ImagesContext} from "../../contexts/ImagesContext";
import useToggle from "../../customHooks/useToggle";
import * as api from "../../services/api";
import ImagesManager from "./ImagesManager/ImagesManager";

type ProductState = {
    name: string;
    description: string;
    price: number;
    region: Region;
    imagesDetails: ImageDetails[];
};

type Action =
    | { type: "SET_FIELD"; field: keyof ProductState; value: string | number }
    | { type: "SET_IMAGES"; images: ImageDetails[] }
    | { type: "RESET" };

const initialState: ProductState = {
    name: "",
    description: "",
    price: 0,
    region: Region.LISBON_AND_TAGUS_VALLEY,
    imagesDetails: [],
};

function productReducer(state: ProductState, action: Action): ProductState {
    switch (action.type) {
        case "SET_FIELD":
            return { ...state, [action.field]: action.value };
        case "SET_IMAGES":
            return { ...state, imagesDetails: action.images };
        case "RESET":
            return initialState;
        default:
            return state;
    }
}


export default function ProductCreationForm(){
    const { user } = useContext(AuthUserContext);
    const { sellerProducts ,setSellerProducts } = useContext(SellerProductsContext);
    const { images } = useContext(ImagesContext);
    const { isToggled, toggle } = useToggle();

    const [state, dispatch] = useReducer(productReducer, initialState);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        dispatch({ type: "SET_IMAGES", images: images });
    }, [images]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        dispatch({ type: "SET_FIELD", field: name as keyof ProductState, value: type === "number" ? Number(value) : value });
    };

    const createProduct = async () => {
        try {
            setLoading(true);
            if (!state.name || !state.description || !state.price || !state.region) {
                setError("All fields are required.");
                setLoading(false);
                return;
            }

            const product = await api.createProduct(state, user.token)

            setSellerProducts([...sellerProducts, product]);
            dispatch({ type: "RESET" });
            toggle();
        } catch (e) {
            setError(e.message || "An error occurred while creating the product.");
        } finally {
            setLoading(false);
        }
    };
    return (
        <div>
            <div>
                <button onClick={toggle}>Create Product</button>
            </div>
            {error && <div style={{ color: "red" }}>{error}</div>}
            {isToggled && (
                <div>
                    <h3>Create Product</h3>
                    <input type="text" name="name" placeholder="Name" value={state.name} onChange={handleChange} />
                    <input type="text" name="description" placeholder="Description" value={state.description} onChange={handleChange} />
                    <input type="number" name="price" placeholder="Price" value={state.price} onChange={handleChange} />
                    <select name="region" value={state.region} onChange={handleChange}>
                        {Object.values(Region).map(region => (
                            <option key={region} value={region}>{region}</option>
                        ))}
                    </select>

                    <ImagesManager />
                    <button onClick={createProduct} disabled={loading || !state.name || !state.description || !state.price}>
                        {loading ? "Creating..." : "Create Product"}
                    </button>
                </div>
            )}
        </div>
    );
}