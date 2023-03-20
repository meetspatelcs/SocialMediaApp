import React from 'react';
import FriendRequestComp from '../Components/FriendRequestComp';



import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';

import { useUser } from '../UserProvider/UserProvider';

const TestFriendsRequests = () => {
    const user = useUser();
  

    return (
        <div>
            <div className='custom-main'>
            <FriendNavbar/>

            <FriendRequestComp compStatus={'Pending request'} />
           
            </div>
            <NavBar />
        </div>
    );
};

export default TestFriendsRequests;