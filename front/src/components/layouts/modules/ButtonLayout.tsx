import {LayoutList, LayoutTemplate} from "lucide-react";
import {ButtonIcon} from "../../modules/Button.tsx";
import {useUserStore} from "@/stores/useUserStore.ts";
import {useMutation} from "react-query";
import {useAxios} from "@config/axios.ts";

export function ButtonLayout() {
    const  {user, setUser} = useUserStore();
    const API = useAxios();

    const {mutate} = useMutation(
        async ({layout}: {layout: boolean}) => {
            await API.post('/profile/update', {
                name: user!.name,
                email: user!.email,
                layout: layout
            })
        }, {
        onSuccess: () => {
            setUser({...user!, layout: !user!.layout})
        }
    })

    const handleClickLayout = (e: MouseEvent) => {
        e.preventDefault();

        mutate({
            layout: !user!.layout
        });
    };

    return <>
        <ButtonIcon
            svg={user!.layout ? LayoutTemplate : LayoutList }
            title="Switch template"
            onClick={handleClickLayout}
        />
    </>
}