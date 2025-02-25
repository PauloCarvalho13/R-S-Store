import * as React from 'react';
import {useContext} from "react";
import {AuthUserContext} from "../contexts/AuthUserContext";
import {Navigate} from "react-router-dom";
import * as api from "../services/api";

type State = {
    loading: boolean;
    error: string | null;
};

type Action =
    | { type: 'Error'; error: string | null }
    | { type: 'Loading'; loading: boolean };

function reduce(state: State, action: Action): State {
    switch (action.type) {
        case 'Error':
            return { ...state, error: action.error };
        case 'Loading':
            return { ...state, loading: action.loading };
        default:
            throw new Error('Unknown action type');
    }
}

export default function Logout(){
    const [state, dispatch] = React.useReducer(reduce, {
            loading: false,
            error: null,
        });

    const {user, setUser} = useContext(AuthUserContext);

    const handleLogout = async () => {
        try {
            if (!user.token) {
                console.error('No auth token found');
                return;
            }

            dispatch({ type: 'Loading', loading: true });

            const response = await api.logout(user.token);


            if (response.ok) {
                setUser(null)
                sessionStorage.clear();
                return <Navigate to={'/'} replace />;
            } else {
                const errorText = await response.text();
                dispatch({ type: 'Error', error: errorText || 'Failed to log out' });
            }
        } catch (error) {
            dispatch({ type: 'Error', error: error.message || 'An error occurred' });
        } finally {
            dispatch({ type: 'Loading', loading: false });
        }
    };

    return (
        <div>
            <h1>Logout</h1>
            {state.error && <div>{state.error}</div>}
            <button onClick={handleLogout} disabled={state.loading}>Logout</button>
        </div>
    );
}