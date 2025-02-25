import * as React from 'react';
import {FormControlLabel, Radio, RadioGroup, Slider} from "@mui/material";
import {useProductFilters} from "../customHooks/useProductsFilters";


export default function FilterProductsForm() {
    const {region, setRegion, priceRange, handlePriceChange} = useProductFilters();
    return (
        <div>
            <h3>Filter Products</h3>

            <div>
                <label>Select a Region:</label>
                <RadioGroup value={region} onChange={(e) => setRegion(e.target.value as Region)}>
                    {Object.values(Region).map((region) => (
                        <FormControlLabel
                            key={region}
                            value={region}
                            control={<Radio/>}
                            label={region}
                        />
                    ))}
                </RadioGroup>
            </div>

            <div>
                <label>Price Range:</label>
                <Slider
                    value={[priceRange.min, priceRange.max]}
                    onChange={handlePriceChange}
                    valueLabelDisplay="auto"
                    min={0}
                    max={5000}
                />
                <span>{priceRange.min} - {priceRange.max}</span>
            </div>
        </div>
    );
}

