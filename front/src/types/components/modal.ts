import { JSX } from 'react';

export interface ModalContextProps {
  showModal: boolean;
  modalContent: () => JSX.Element;
  modalSize: string;
  openModal: (content: () => JSX.Element, size?: string) => void;
  closeModal: () => void;
}

export interface ModalProviderProps {
  children: JSX.Element | JSX.Element[];
}
