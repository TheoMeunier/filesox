import {Outlet} from "react-router-dom";
import {Tab, TabItem, TabItems} from "@components/modules/Tab.tsx";
import {Archive, Share2, User} from "lucide-react";
import {useTranslation} from "react-i18next";

export function Profile() {
    const {t} = useTranslation();

    return <>
        <Tab>
            <TabItems>
                <TabItem link="/profile">
                    <User size={20}/>
                    {t('title.nav.profile')}
                </TabItem>
                <TabItem link="/profile/share">
                    <Share2 size={20}/>
                    {t('title.nav.shares')}
                </TabItem>
                <TabItem link="/profile/logs">
                    <Archive size={20}/>
                    {t('title.nav.logs')}
                </TabItem>
            </TabItems>
        </Tab>

        <Outlet/>
    </>;
}