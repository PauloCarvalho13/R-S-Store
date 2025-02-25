import * as React from 'react';
import {useContext, useEffect} from 'react'
import * as api from '../services/api';
import {ProductsContext} from "../contexts/ProductsContext";
import {useNavigate} from "react-router-dom";
import {useFetch} from "../customHooks/useFetcher";

export default function ProductsList() {
    const { products, setProducts } = useContext(ProductsContext);
    const { data, loading, error } = useFetch(api.getProducts, []);
    const navigate = useNavigate();

    useEffect(() => {
        if (data) setProducts(data);
    }, [data]);

    return (
        <div>
            <h1>Products</h1>
            {loading && <p>Loading...</p>}
            {error && <p>Error: {error}</p>}
            {!loading && !error && products.length > 0 ? (
                <ul>
                    {products.map((product) => (
                        <li key={product.id} onClick={() => navigate(`/product/${product.id}`)}>
                            <h2>{product.name}</h2>
                            <p><strong>Description:</strong> {product.description}</p>
                            <p><strong>Price:</strong> ${product.price}</p>
                            <p><strong>Region:</strong> {product.region}</p>
                            <img src={product.image.url} alt="Product Image" />
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No products available</p>
            )}
        </div>
    );
}