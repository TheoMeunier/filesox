import {Outlet} from "react-router-dom";

export function AdminProtectedRouteProvider() {
    return <>
        <Outlet/>
    </>
}