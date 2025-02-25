import * as React from "react";
import {useEffect, useContext} from 'react';
import * as api from '../../services/api';
import {useFetch} from "../../customHooks/useFetcher";
import {AuthUserContext} from "../../contexts/AuthUserContext";
import {SellerProductsContext} from "../../contexts/SellerProductsContext";
import DashBoardElement from "./DashBoardElement";
import ProductCreationForm from "./ProductCreationForm";

export default function SellerDashboard() {
  const { user } = useContext(AuthUserContext);
  const {sellerProducts, setSellerProducts} = useContext(SellerProductsContext)
  const {data: products, error, loading} = useFetch(() => api.getMyProducts(user.token), []);

  useEffect(() => {
    if (products) setSellerProducts(products)
  }, [products]);

  return (
    <div>
      {loading && <p>Loading...</p>}
      {error && <p>Error: {error}</p>}
      <h2>Your DashBoard</h2>
      <ProductCreationForm />
      {products.length === 0 ? (
        <p>No products announced yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Region</th>
              <th>Images</th>
            </tr>
          </thead>
          <tbody>
            { sellerProducts.map(product => <DashBoardElement elem={product}/> ) }
          </tbody>
        </table>

      )}
    </div>
  );
}


