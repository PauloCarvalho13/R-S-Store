import * as React from 'react';
import useSlideShow from "../customHooks/useSlideShow";

const delayBetweenImages = 3000;

export default function SlideShow({imgSources}: {imgSources: Array<string>}) {
    const {currentImageIndex, next, previous} = useSlideShow({imgSources}, delayBetweenImages);
    return (
        <div>
            <img src={imgSources[currentImageIndex]} alt="Product Image" />
            <button onClick={next} disabled={currentImageIndex == imgSources.length}>
                Next
            </button>
            <button onClick={previous} disabled={currentImageIndex == 0}>
                Previous
            </button>
        </div>
    );
}