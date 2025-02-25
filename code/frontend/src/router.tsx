import * as React from 'react';
import { createBrowserRouter, RouterProvider, useParams } from 'react-router-dom'
import {createRoot} from "react-dom/client";
import ProductDetails from './components/ProductDetails';
import Login from "./components/Login";
import Logout from "./components/Logout.js";
import { ProductsProvider} from "./contexts/ProductsContext";
import {AuthRequire} from "./contexts/AuthRequire";
import {SellerProductsProvider} from "./contexts/SellerProductsContext";
import SellerDashboard from "./components/dashboard/SellerDashboard";
import {ImagesProvider} from "./contexts/ImagesContext";
import {AuthUserProvider} from "./contexts/AuthUserContext";
import HomeLayout from "./components/HomeLayout";


const router = createBrowserRouter([{
  path: '/',
  element:
      <ProductsProvider>
        <Home />
      </ProductsProvider>,
  children: [
    {
      path: 'product/:id',
      element: <ProductDetailsRender />,
    },
    {
          path: 'login',
          element: <AuthUserProvider> <Login /> </AuthUserProvider>
      },
    {
      path: 'logout',
      element:
        <AuthUserProvider>
          <AuthRequire>
            <Logout />
          </AuthRequire>
        </AuthUserProvider>
    },
    {
        path: 'my-dashboard',
        element:

            <AuthRequire>
                <SellerProductsProvider>
                    <ImagesProvider>
                        <SellerDashboard/>
                    </ImagesProvider>
                </SellerProductsProvider>
            </AuthRequire>,
    }
  ]
}])

function Home() {
  return (<HomeLayout/>)
}

function ProductDetailsRender() {
    const { id: productId } = useParams()
    return <ProductDetails id={Number(productId)} />
}


export function router_app() {
  createRoot(document.getElementById("container")).render(
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
  )
}

