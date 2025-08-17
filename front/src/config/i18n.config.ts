import i18n from "i18next";
import {initReactI18next} from "react-i18next";
import {fr} from "@/lang/fr.ts";
import {en} from "@/lang/en.ts";

export const i18nConfig = i18n.use(initReactI18next)
    .init({
        resources: {
            fr: fr,
            en: en
        },
        lng: 'fr',
        fallbackLng: 'en',
        interpolation: {
            escapeValue: false
        }
    });