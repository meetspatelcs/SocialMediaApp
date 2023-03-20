import React from 'react';
import FriendAddComp from '../Components/Friends/FriendAddComp';
import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';

const FriendsAdd = () => {
    return (
        <div>
            <div className='custom-main'>
                <FriendNavbar />

                <FriendAddComp />
            </div>
            <NavBar />
        </div>
    );
};

export default FriendsAdd;