import * as React from 'react';
import {useContext, useState} from "react";
import * as api from "../../../services/api";
import {AuthUserContext} from "../../../contexts/AuthUserContext";
import {ImagesContext} from "../../../contexts/ImagesContext";

export default function ImagePreview({ image }: { image: ImageDetails }) {
    const { user } = useContext(AuthUserContext);
    const { images ,setImages } = useContext(ImagesContext);

    const [error, setError] = useState<string | null>(null);
    const [isDeleting, setIsDeleting] = useState<boolean>(false);

    const deleteImage = async () => {
        try {
            setIsDeleting(true);
            await api.deleteImage(image.id, user.token);
            setIsDeleting(false);
            setImages(images.filter(img => img.id !== image.id));
        } catch (e) {
            setError(e.message);
            setIsDeleting(false);
        }
    }

    return (
        <div>
            <img src={image.url} alt="product" />
            <button onClick={deleteImage} disabled={isDeleting}>Delete</button>
            {error && <div>{error}</div>}
        </div>
    );

}