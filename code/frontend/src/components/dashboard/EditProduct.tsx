import * as React from "react";
import {AuthUserContext} from "../../contexts/AuthUserContext";
import {FormEvent, useContext, useEffect, useReducer, useState} from "react";
import useToggle from "../../customHooks/useToggle";
import {ImagesContext} from "../../contexts/ImagesContext";
import ImagesManager from "./ImagesManager/ImagesManager";
import * as api from "../../services/api";


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
    | { type: "RESET"; product: SellerProductInfo };

const productReducer = (state: ProductState, action: Action): ProductState => {
    switch (action.type) {
        case "SET_FIELD":
            return { ...state, [action.field]: action.value };
        case "SET_IMAGES":
            return { ...state, imagesDetails: action.images };
        case "RESET":
            return {
                name: action.product.name,
                description: action.product.description,
                price: action.product.price,
                region: action.product.region as Region,
                imagesDetails: action.product.imagesDetails,
            };
        default:
            return state;
    }
};
export default function EditProduct({product}:{product: SellerProductInfo}){
    const { user } = useContext(AuthUserContext);
    const {setImages} = useContext(ImagesContext);
    const {isToggled, toggle} = useToggle();

    const [state, dispatch] = useReducer(productReducer, {
        name: product.name,
        description: product.description,
        price: product.price,
        region: product.region as Region,
        imagesDetails: product.imagesDetails,
    });

    const [ error, setError ] = useState<string | null>(null);

    useEffect(() => {
        if (isToggled) {
            dispatch({ type: "RESET", product });
            setImages(product.imagesDetails);
        } else {
            setImages(null);
        }
    }, [isToggled, product]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        dispatch({ type: "SET_FIELD", field: name as keyof ProductState, value: type === "number" ? Number(value) : value });
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        try {
            setError(null);
            await api.updateProduct(product.id, state, user.token);
            toggle();
        } catch (error) {
            setError(error.message)
        }
    };

    return (
        <div>
            <h1>Edit Product</h1>
            <button onClick={toggle}>Edit</button>
            {isToggled && error && <p>{error}</p>}
            {isToggled && (
                <form onSubmit={handleSubmit}>
                    <label>Name</label>
                    <input type="text" name="name" value={state.name} onChange={handleChange} />

                    <label>Description</label>
                    <input type="text" name="description" value={state.description} onChange={handleChange} />

                    <label>Price</label>
                    <input type="number" name="price" value={state.price} onChange={handleChange} />

                    <label>Region</label>
                    <select name="region" value={state.region} onChange={handleChange}>
                        {Object.values(Region).map(region => (
                            <option key={region} value={region}>{region}</option>
                        ))}
                    </select>

                    <button type="submit">Submit</button>
                </form>
            )}
            {isToggled && <ImagesManager/>}
        </div>
    );
}