import React, { useState } from 'react';
import NavBar from '../NavBar/NavBar';
import { useUser } from '../UserProvider/UserProvider';
import { useEffect } from 'react';
import requestToPath from '../Service/fetchService';
import { useParams } from 'react-router-dom';
import UserViewOnPage from './UserViewOn/Page';
import PageMain from '../Components/Pages/PageMain';

const Page = () => {

  const user = useUser();
  const {pageId} = useParams();

  const [pageRole, setPageRole] = useState(null);

  const default_role = 'ROLE_USER';
  const role_A = 'ROLE_ADMIN';

  useEffect(() => {
    requestToPath(`/api/pages/${pageId}/userRole`, "GET", user.jwt)
    .then((roleResponse) => { setPageRole(roleResponse.pageRole); })
    .catch((error) => { console.log(error); })
  }, [])

  function displayContent(){
    if(pageRole == role_A){
      return <PageMain userRole = {pageRole}/>
    }
    else if(pageRole == default_role){
      return <UserViewOnPage userRole = {pageRole} />
    }
    return <UserViewOnPage userRole = {null} />
  }
  
  return (
      <div>
        {displayContent()}
            <NavBar />
      </div>
  );
};

export default Page;