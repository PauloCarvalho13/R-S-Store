import * as React from 'react';
import DeleteDialog from "./DeleteDialog";
import EditProduct from "./EditProduct";

export default function DashBoardElement({elem} : { elem: SellerProductInfo }){
    return (
        <tr key={elem.id}>
            <td>{elem.name}</td>
            <td>{elem.description}</td>
            <td>{elem.price}</td>
            <td>{elem.region}</td>
            <td>
                <EditProduct product={elem}/>
                <DeleteDialog product={elem}/>
            </td>
        </tr>
    );
}