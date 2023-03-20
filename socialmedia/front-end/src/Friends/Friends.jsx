import React from 'react';
import { useEffect } from 'react';
import { useState } from 'react';
import FriendAddComp from '../Components/Friends/FriendAddComp';
import FriendListComp from '../Components/Friends/FriendListComp';
import FriendRequestComp from '../Components/Friends/FriendRequestComp';
import FriendNavbar from '../NavBar/FriendNavbar';
import NavBar from '../NavBar/NavBar';
import { useUser } from '../UserProvider/UserProvider';

const Friends = () => {
    const user = useUser();

    const default_Friend = 'Friends';
    const default_Pending = 'Pending'

    return (
        <div>
            <div className='custom-main'>
                <FriendNavbar />

                <div className='ms-1 me-1'>
                    <FriendRequestComp compStatus={default_Pending} />

                    <FriendListComp compStatus={default_Friend} />
                </div>
                
                <br/> <br/>
            </div>
            <NavBar />
       
        </div>
    );
};

export default Friends;