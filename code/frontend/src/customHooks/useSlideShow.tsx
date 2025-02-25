import {useEffect, useState} from "react";

export default function useSlideShow({imgSources}: {imgSources: Array<string>}, delay: number):
    {currentImageIndex: number, next: () => void, previous: () => void} {
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    useEffect(() => {
        let intervalId

        intervalId = setInterval(() => {
            if (currentImageIndex != imgSources.length)
                setCurrentImageIndex(currentImageIndex + 1)
        }, delay)

        return () => { clearInterval(intervalId) }
    }, [setCurrentImageIndex]);

    const next = () => setCurrentImageIndex(currentImageIndex + 1)
    const previous = () => setCurrentImageIndex(currentImageIndex - 1)

    return {currentImageIndex, next, previous}
}