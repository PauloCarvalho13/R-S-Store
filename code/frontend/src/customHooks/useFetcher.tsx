import { useReducer, useEffect } from "react";

type State<T> = {
    data: T | null;
    loading: boolean;
    error: string | null;
};

type Action<T> =
    | { type: "FETCH_LOADING" }
    | { type: "FETCH_SUCCESS"; payload: T }
    | { type: "FETCH_ERROR"; payload: string };

function fetchReducer<T>(state: State<T>, action: Action<T>): State<T> {
    switch (action.type) {
        case "FETCH_LOADING":
            return { data: state.data, loading: true, error: null };
        case "FETCH_SUCCESS":
            return { data: action.payload, loading: false, error: null };
        case "FETCH_ERROR":
            return { data: state.data, loading: false, error: action.payload };
    }
}

export function useFetch<T>(fetchFunction: () => Promise<T>, dependencies: any[] = []) {
    const [state, dispatch] = useReducer(fetchReducer<T>, {
        data: null,
        loading: false,
        error: null,
    });

    useEffect(() => {
        const fetchData = async () => {
            dispatch({ type: "FETCH_LOADING" });
            try {
                const result = await fetchFunction();
                dispatch({ type: "FETCH_SUCCESS", payload: result });
            } catch (error) {
                dispatch({ type: "FETCH_ERROR", payload: (error as Error).message });
            }
        };

        fetchData();
    }, dependencies);

    return state;
}
