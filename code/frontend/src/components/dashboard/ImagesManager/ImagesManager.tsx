import * as React from 'react';
import ImagePreview from "./ImagePreview";
import {useContext} from "react";
import {ImagesContext} from "../../../contexts/ImagesContext";
import ImageUploader from "./ImageUploader";

const maxFiles = 5;

export default function ImagesManager () {
    const { images } = useContext(ImagesContext);
    return (
        <div>
            <h1>Images Manager</h1>
            {images && images.map(img => <ImagePreview image={img}/>)}
            {images && images.length < maxFiles && (<ImageUploader/>)}
        </div>
    );
}