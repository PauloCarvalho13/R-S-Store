import * as React from 'react';
import {useContext, useState} from "react";
import {AuthUserContext} from "../../../contexts/AuthUserContext";
import {ImagesContext} from "../../../contexts/ImagesContext";
import * as api from "../../../services/api";

const validFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];
const maxFileSize = 1024 * 1024 * 5; // 5MB

export default function ImageUploader() {
    const { user } = useContext(AuthUserContext);
    const { images, setImages } = useContext(ImagesContext);

    const [error, setError] = useState<string | null>(null);
    const [uploading, setIsUploading] = useState<boolean>(false);

    const uploadImage = async (file: File) => {
        if (!validFileTypes.includes(file.type)) {
            setError("Formato de ficheiro inválido. Apenas JPEG e PNG são permitidos.");
            return;
        }

        if (file.size > maxFileSize) {
            setError("O ficheiro é demasiado grande. O tamanho máximo permitido é 5MB.");
            return;
        }

        try {
            setIsUploading(true);
            setError(null);

            const { id, uri } = await api.uploadImage(file, user.token);

            setImages([...images, { id, url: uri }]);
        } catch (e) {
            setError(e.message || "Ocorreu um erro ao enviar a imagem.");
        } finally {
            setIsUploading(false);
        }
    };

    return (
        <div>
            <h1>Image Uploader</h1>
            {error && <p>{error}</p>}
            {uploading && <p>A carregar imagem...</p>}
            <input
                type="file"
                accept={validFileTypes.join(',')}
                onChange={e => {
                    const file = e.target.files?.[0];
                    if (file) uploadImage(file);
                }}
                disabled={uploading}
            />
        </div>
    );
}
