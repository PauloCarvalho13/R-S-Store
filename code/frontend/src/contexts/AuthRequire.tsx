import * as React from "react"
import { Navigate, useLocation } from "react-router-dom"
import {AuthUserContext} from "./AuthUserContext";

export function AuthRequire({ children }: { children: React.ReactNode }) {
    const { user } = React.useContext(AuthUserContext)
    const location = useLocation()
    if (user) { return <>{children}</> }
    else {
        return <Navigate to={"/login"} state={{source: location.pathname}}></Navigate>
    }
}