import React from 'react';
import FriendListComp from '../Components/FriendListComp';
import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';

const TestFriendsAll = () => {
    return (
        <div>
            <FriendNavbar/>
            <FriendListComp compStatus={'Accepted request'} />
            <NavBar />
        </div>
    );
};

export default TestFriendsAll;