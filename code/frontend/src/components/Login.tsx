import * as React from 'react';
import {AuthUserContext, User} from "../contexts/AuthUserContext";
import {Navigate, useLocation} from "react-router-dom";
import {useContext, useEffect} from "react";
import * as api from "../services/api";

type State =
    | { tag: 'editing'; error?: string; inputs: { email: string; password: string } }
    | { tag: 'submitting' }
    | { tag: 'redirect' };

type Action =
    | { type: 'edit'; inputName: string; inputValue: string }
    | { type: 'submit' }
    | { type: 'success' }
    | { type: 'error'; message: string };

function reduce(state: State, action: Action): State {
    switch (state.tag) {
        case 'editing':
            switch (action.type) {
                case 'edit':
                    return {
                        tag: 'editing',
                        inputs: { ...state.inputs, [action.inputName]: action.inputValue },
                    };
                case 'submit':
                    return { tag: 'submitting' };
                default:
                    return state;
            }
        case 'submitting':
            switch (action.type) {
                case 'success':
                    return { tag: 'redirect' };
                case 'error':
                    return {
                        tag: 'editing',
                        error: action.message,
                        inputs: { email: '', password: '' },
                    };
                default:
                    return state;
            }
        case 'redirect':
            throw new Error("Already in final state 'redirect' and should not reduce to any other state.");
    }
}


export default function Login() {
    const location = useLocation();
    const { user, setUser } = useContext(AuthUserContext);

    const [state, dispatch] = React.useReducer(reduce, {
        tag: 'editing',
        inputs: {
            email: '',
            password: '',
        },
    });

    // check if there's an old user in the storage
    useEffect(() => {
        // only check if no user is already in the context
        if (!user) {
            const storedUser = sessionStorage.getItem('authUser');
            if (storedUser) {
                const parsedUser: User = JSON.parse(storedUser);
                setUser(parsedUser);
            }
        }
    }, [user, setUser]);

    const fromLogout = location.state?.source?.startsWith('/home/channels/');
    if (user || state.tag === 'redirect' ) {
        if (fromLogout) return <Navigate to="/home" replace />;
        return <Navigate to={location.state?.source || '/home'} replace />;
    }

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (state.tag !== 'editing') return;

        dispatch({ type: 'submit' });

        const { email, password } = state.inputs;
        try {
            // check if there's a valid user in storage already
            const oldUserString = sessionStorage.getItem('authUser');

            const oldUser = oldUserString ? JSON.parse(oldUserString) as User : null;

            const user = oldUser ? oldUser : await (async () => {

                const loginResponse = await api.login(email, password);

                if (!loginResponse.ok) {
                    const errorText = await loginResponse.text();
                    dispatch({ type: 'error', message: errorText });
                }

                const newToken = await loginResponse.text();

                const userInfo = api.userInfo(newToken);

                sessionStorage.clear();
                sessionStorage.setItem('authUser', JSON.stringify(userInfo));

                return userInfo
            })()

            dispatch({ type: 'success' });
        } catch (error: any) {
            dispatch({ type: 'error', message: error.message });
        }
    };

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        dispatch({ type: 'edit', inputName: name, inputValue: value });
    };

    const email = state.tag === 'editing' ? state.inputs.email : '';
    const password = state.tag === 'editing' ? state.inputs.password : '';


    return (
        <div>
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="email"
                    placeholder="Email"
                    value={email}
                    onChange={handleChange}
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={password}
                    onChange={handleChange}
                />
                {state.tag === 'editing' && state.error && <p>{state.error}</p>}
                <button type="submit" disabled={state.tag === 'submitting'}>
                    {state.tag === 'submitting' ? 'Logging in...' : 'Login'}
                </button>
            </form>
        </div>
    )
}