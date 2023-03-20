import React from 'react';
import FriendRequestComp from '../Components/Friends/FriendRequestComp';
import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';

const FriendsRequests = () => {
    return (
        <div>
            <div className='custom-main'>
            <FriendNavbar/>

            <FriendRequestComp compStatus={'Pending'} />
           
            </div>
            <NavBar />
        </div>
    );
};

export default FriendsRequests;