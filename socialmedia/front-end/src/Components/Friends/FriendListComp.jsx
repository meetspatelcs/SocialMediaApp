import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import FriendRequests from '../../Cards/FriendRequests';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

import './friends.css';

const FriendListComp = (props) => {
    const user = useUser();

    const {compStatus} = props;
    const {userId} = useParams();
    
    const[friendList, setFriendList] = useState([]);
   
    useEffect(() => {
        requestToPath(`/api/friends/user/${userId}`, "GET", user.jwt)
            .then((friendResponse) => {
                console.log(friendResponse);
                setFriendList(friendResponse);
            })
            .catch((error) => {console.log(error);})
    }, [])
   
    function displayFriends(){
        if(friendList != ''){
            return friendList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachFriend) => {
                    return <FriendRequests key={eachFriend.id} selectedFriendId={eachFriend.id} 
                    firstname={eachFriend.firstname} lastname={eachFriend.lastname} 
                    compStatus={compStatus} />
                })
        }
        else{
            return <div>No data</div>
        }
    }

    return (
        <div className='container friendLC-RC-wrapper mt-5 mb-5'>
            <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Friends</h2>
            <div className=" friendLC-RC-eachFriend justify-content-center">
                {displayFriends()}
            </div>
        </div>
    );
}

export default FriendListComp;