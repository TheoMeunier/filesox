type PermissionOption = {
    value?: string;
    label?: string;
    disabled?: boolean;
    isSelected?: boolean;
};

export type Permissions = PermissionOption[] | undefined;