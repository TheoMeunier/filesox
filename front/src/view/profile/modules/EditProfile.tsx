import {EditProfileInformation} from "../partials/EditProfileInformation.tsx";
import {EditProfilePassword} from "../partials/EditProfilePassword.tsx";

export function ProfileEdit() {

  return <>
      <div className="px-7 py-4 w-2/4 space-y-6">
          <EditProfileInformation/>
          <EditProfilePassword/>
      </div>
  </>;
}