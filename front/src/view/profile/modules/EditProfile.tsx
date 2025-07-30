import {EditProfileInformation} from "../partials/EditProfileInformation.tsx";
import {EditProfilePassword} from "../partials/EditProfilePassword.tsx";
import {Row} from "@components/modules/Grid.tsx";

export function ProfileEdit() {

  return <>
      <div className="px-7 py-4">
          <Row cols={2}>
              <EditProfileInformation/>
              <EditProfilePassword/>
          </Row>
      </div>
  </>;
}