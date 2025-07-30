import React, {ReactNode} from "react";
import './alerts.css'
import {CircleAlert, CircleCheck, CircleX} from "lucide-react";

export function Alerts({children}: { children: ReactNode }) {
    return (
        <div className={`alerts`} role="alert">
            {children}
        </div>
    );
}

export function Alert({children, type}: { children: ReactNode, type: AlertTypeProps }) {
    const className = `alert after:bg-${getAlertClass(type)}-500`;
    return (
        <div className={className}>
            {children}
        </div>
    );
}

export function AlertIcon({type}: { type: AlertTypeProps }) {
    const icon = getAlertIcon(type);
    return <>
        {icon}
    </>;
}

// function for flashing alerts
export type AlertTypeProps = 'success' | 'danger' | 'warning';

function getAlertClass(type: AlertTypeProps): string {
    const alertClasses: { [key in AlertTypeProps]: string } = {
        success: 'after:bg-green-500',
        danger: 'after:bg-red-500',
        warning: 'after:bg-orange-500',
    };

    return alertClasses[type];
}

function getAlertIcon(type: AlertTypeProps): React.ReactNode {
    const alertIcons: { [key in AlertTypeProps]: React.ReactNode } = {
        success: <CircleCheck className="text-green-500"/>,
        danger: <CircleX className="text-red-500"/>,
        warning: <CircleAlert className="text-orange-500"/>,
    };

    return alertIcons[type] || null;
}