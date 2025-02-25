import * as React from "react";
import useToggle from "../../customHooks/useToggle";
import {useContext, useState} from "react";
import {AuthUserContext} from "../../contexts/AuthUserContext";
import * as api from "../../services/api";
import {SellerProductsContext} from "../../contexts/SellerProductsContext";

export default function DeleteDialog({product}:{ product: SellerProductInfo}) {
    const {user} = useContext(AuthUserContext);
    const {sellerProducts,setSellerProducts} = useContext(SellerProductsContext);
    const {isToggled, toggle} = useToggle();
    const [error, setError] = useState<string | null>(null);

    const deleteProduct = async () => {
        try{
            await api.deleteProduct(product.id, user.token);
            setSellerProducts(sellerProducts.filter((p) => p.id !== product.id));
        }catch (e) {
            setError(e.message);
        }
    }

    return (
        <div>
            <div>
                <button onClick={toggle}>Delete</button>
            </div>
            {isToggled && (
                <div>
                    <p>Are you sure you want to delete {product.name}?</p>
                    <button onClick={deleteProduct}>Yes</button>
                    <button onClick={toggle}>Cancel</button>
                </div>
            )}
            {error && <p>{error}</p>}
        </div>
    );
}