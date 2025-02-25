import { useState, useEffect, useContext } from "react";
import { ProductsContext } from "../contexts/ProductsContext";
import * as api from '../services/api';

export function useProductFilters() {
    const { setProducts } = useContext(ProductsContext);
    const [region, setRegion] = useState<Region | null>(null);
    const [priceRange, setPriceRange] = useState<PriceRange>({ min: 0, max: 1000 });

    const handlePriceChange = (_event: Event, newValue: number | number[]) => {
        if (Array.isArray(newValue)) {
            setPriceRange({ min: newValue[0], max: newValue[1] });
        }
    };

    useEffect(() => {
        if (region !== null) {
            const search = async () => {
                const res = await api.getProductsByRegionAndPrice(region, priceRange.min, priceRange.max);
                setProducts(res);
            };
            search();
        }
    }, [region, priceRange]);

    return { region, setRegion, priceRange, handlePriceChange };
}