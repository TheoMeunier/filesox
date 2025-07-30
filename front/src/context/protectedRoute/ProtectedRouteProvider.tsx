import {Navigate, Outlet} from "react-router-dom";
import {useAuth} from "../modules/AuthContext.tsx";

export function ProtectedRouteProvider() {
    const {token} = useAuth()

    if (!token) {
        return <Navigate replace to="/login"/>
    }

    return <>
        <Outlet/>
    </>
}