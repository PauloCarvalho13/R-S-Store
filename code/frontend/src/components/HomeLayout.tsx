import * as React from 'react';
import { Outlet } from 'react-router-dom';
import ProductsList from "./ProductList";
import FilterProductsForm from "./FilterProductsForm";
import Login from "./Login";
import {useContext} from "react";
import {AuthUserContext} from "../contexts/AuthUserContext";
import Logout from "./Logout";

export default function HomeLayout() {
    const {user} = useContext(AuthUserContext);
    return (
        <div>
            <h1>Welcome to the store</h1>
            <ProductsList />
            <FilterProductsForm/>
            {user? <h2>Welcome {user.username}</h2> : null}
            {user ? null : <Login />}
            {user && <Logout/> }
            <Outlet />
        </div>
    )
}