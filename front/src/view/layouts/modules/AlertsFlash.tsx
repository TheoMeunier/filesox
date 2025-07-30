import {Alert, AlertIcon, Alerts, AlertTypeProps} from "@components/modules/Alerts/Alert.tsx";
import {AlertType, useAlerts} from "@context/modules/AlertContext.tsx";
import {useEffect, useRef} from "react";

export function AlertsFlash() {
    const {alerts, deleteAlert} = useAlerts()
    const timersRef = useRef<Record<number, NodeJS.Timeout>>({});

    useEffect(() => {
        alerts.forEach((_, index) => {
            if (!timersRef.current[index]) {
                timersRef.current[index] = setTimeout(() => {
                    deleteAlert(index);
                    delete timersRef.current[index];
                }, 5000);
            }
        });

        return () => {
            Object.values(timersRef.current).forEach(clearTimeout);
            timersRef.current = {};
        };
    }, [alerts, deleteAlert]);

    return <Alerts>
        {alerts.map((alert: AlertType, index) => {
            return <Alert key={index} type={alert.type as AlertTypeProps}>
                <AlertIcon type={alert.type as AlertTypeProps}/>
                {alert.message}
            </Alert>
        })}
    </Alerts>;
}