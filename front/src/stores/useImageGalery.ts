import {create} from "zustand";

type State = {
    showImageGallery: Boolean;
    imageGalleryIndex: number;
};

interface Action {
    setImageGalleryShow: (showImageGallery: boolean) => void;
    setImageGalleryIndex: (imageGalleryIndex: number) => void;
    setImageGallery: (showImageGallery: boolean, imageGalleryIndex: number) => void;
}

export const useImageGalleryStore = create<State & Action>((set) => ({
    showImageGallery: false,
    imageGalleryIndex: 0,
    setImageGalleryShow: (showImageGallery: boolean) => set({ showImageGallery }),
    setImageGalleryIndex: (imageGalleryIndex: number) => set({ imageGalleryIndex }),
    setImageGallery: (showImageGallery: boolean, imageGalleryIndex: number) => set({showImageGallery,  imageGalleryIndex }),
}));