import React from 'react';
import FriendListComp from '../Components/Friends/FriendListComp';
import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';

const FriendsAll = () => {

    const default_Friend = 'Friends'

    return (
        <div>
            <div className='custom-main'>
                <FriendNavbar/>
                <FriendListComp compStatus={default_Friend} />
            </div>
            <NavBar />
        </div>
    );
};

export default FriendsAll;