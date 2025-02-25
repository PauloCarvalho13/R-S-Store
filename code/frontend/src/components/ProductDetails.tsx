import  *  as React from 'react';
import * as api from '../services/api'
import {useFetch} from "../customHooks/useFetcher";
import SlideShow from "./SlideShow";
import AnnouncerInfo from "./AnnouncerInfo";

function ProductDetails({id}: {id: number}) {
  const { data: product, loading, error } = useFetch(() => api.getProductById(id), [id]);

  return (
    <div>
      {loading && <div>Loading...</div>}
      {error && <div>Error: {error}</div>}
      {product ? (
        <div>
          <SlideShow imgSources={product.imagesDetails.map(img => img.url)} />
          <h2>{product.name}</h2>
          <p>{product.description}</p>
          <p>Price: ${product.price}</p>
          <p>Region: {product.region}</p>
          <AnnouncerInfo announcer={product.announcer} />
        </div>
      ) : (
        <div>Product not found</div>
      )}
    </div>
  );
}

export default ProductDetails;
