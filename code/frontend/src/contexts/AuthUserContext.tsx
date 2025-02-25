import * as React from "react"
import {createContext, useEffect, useState} from "react"
import Cookies from "js-cookie";


export type User = {
    id: number,
    email: string,
    username: string,
    token: string
}

type AuthUserContextType = {
    user: User | null,
    setUser: (user: User) => void
}

// Create the context
export const AuthUserContext = createContext<AuthUserContextType>({
    user: null,
    setUser: () => { throw Error("Not implemented!") }
})

// Define a Provider Component
export function AuthUserProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const [user, setUser] = useState<User|null>(null)
    // Load user from cookie on mount
    useEffect(() => {
        const token = Cookies.get("auth_token");
        const storedUser:User = Object(sessionStorage.getItem("authUser"))
        const authUser = {id: storedUser.id, email: storedUser.email, username: storedUser.username, token: token}

        if (token && storedUser) {
            setUser(authUser);
        }
    }, []);
    return (
        <AuthUserContext.Provider value={{ user, setUser }}>
            {children}
        </AuthUserContext.Provider>
    )
}