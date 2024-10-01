import {Outlet} from "react-router-dom";
import {Tab, TabItem, TabItems} from "@components/modules/Tab.tsx";
import {Archive, Share2, User} from "lucide-react";

export function Profile() {
    return <>
        <Tab>
            <TabItems>
                <TabItem link="/profile">
                    <User size={20}/>
                    Profile
                </TabItem>
                <TabItem link="/profile/share">
                    <Share2 size={20}/>
                    Share
                </TabItem>
                <TabItem link="/profile/logs">
                    <Archive size={20}/>
                    Logs
                </TabItem>
            </TabItems>
        </Tab>

        <Outlet/>
    </>;
}