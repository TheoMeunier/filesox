import './assets/style/app.css'
import React from 'react'
import ReactDOM from 'react-dom/client'
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Login from "./view/auth/Login.tsx";
import Register from "./view/auth/Register.tsx";
import {App} from "./view/layouts/App.tsx";
import {Dashboard} from "./view/Dashboard.tsx";
import {Profile} from "./view/profile/Profile.tsx";
import {ProfileEdit} from "./view/profile/modules/EditProfile.tsx";
import {ProfileShare} from "./view/profile/modules/ShareProfile.tsx";
import {ProtectedRouteProvider} from "./context/protectedRoute/ProtectedRouteProvider.tsx";
import {ProfileLog} from "./view/profile/modules/LogProfile.tsx";
import {AlertsProvider} from "./context/modules/AlertContext.tsx";
import {AdminProtectedRouteProvider} from "./context/protectedRoute/AdminProtectedRouteProvider.tsx";
import {AdminUsers} from "./view/admin/users/AdminUsers.tsx";
import {AdminSettings} from "./view/admin/AdminSettings.tsx";
import {AdminShares} from "./view/admin/AdminShares.tsx";
import {AdminLogs} from "./view/admin/AdminLogs.tsx";
import {AuthProvider} from "./context/modules/AuthContext.tsx";
import {CurrentPathProvider} from "./context/modules/CurrentPathContext.tsx";
import {QueryClientProvider} from "@tanstack/react-query";
import {i18nConfig} from "@config/i18n.config.ts";
import {queryClient} from "@config/react-query.config.ts";

i18nConfig()

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <AlertsProvider>
                <AuthProvider>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/" element={<ProtectedRouteProvider/>}>
                                <Route path="/" element={<App/>}>
                                    <Route path="/" element={<CurrentPathProvider/>}>
                                        <Route index element={<Dashboard/>}/>
                                    </Route>

                                    <Route path="/profile" element={<Profile/>}>
                                        <Route index element={<ProfileEdit/>}/>
                                        <Route path="share" element={<ProfileShare/>}/>
                                        <Route path="logs" element={<ProfileLog/>}/>
                                    </Route>

                                    <Route path="/admin" element={<AdminProtectedRouteProvider/>}>
                                        <Route path="settings" element={<AdminSettings/>}/>
                                        <Route path="users" element={<AdminUsers/>}/>
                                        <Route path="shares" element={<AdminShares/>}/>
                                        <Route path="logs" element={<AdminLogs/>}/>
                                    </Route>
                                </Route>
                            </Route>

                            <Route path="/login" element={<Login/>}/>
                            <Route path="/register" element={<Register/>}/>
                        </Routes>
                    </BrowserRouter>
                </AuthProvider>
            </AlertsProvider>
        </QueryClientProvider>
    </React.StrictMode>
)
