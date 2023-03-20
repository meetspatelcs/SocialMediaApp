import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';
import NavBar from '../NavBar/NavBar';
import requestToPath from '../Service/fetchService';
import { Button, Card } from 'react-bootstrap';
import FriendRequests from '../Cards/FriendRequests';
import FriendNavbar from '../NavBar/FriendNavbar';
import FriendRequestComp from '../Components/FriendRequestComp';
import FriendListComp from '../Components/FriendListComp';


const TestFriends = () => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
    

    const[friendList, setFriendList] = useState(null);
    
    useEffect(() => {
        requestToPath(`/api/friends`, "GET", user.jwt)
        .then((friendsData) => {
            // console.log(friendsData);
            setFriendList(friendsData);
        })
    }, []);

    return (
        <div>
            <div className='custom-main'>
                
                <FriendNavbar />
                {/* <div className='container friend-wrapper mt-5 mb-5'>
                    <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Request</h2>
                    <div className="d-grid gap-5 justify-content-center" style={{gridTemplateColumns:"repeat(auto-fill, 15rem)"}}>
                        {friendList ? friendList.filter(eachFriend => eachFriend.status === 'new').map((eachFriend) => ( 
                            <FriendRequests key={eachFriend.id} friendId = {eachFriend.id} selectedrequestedUser={eachFriend.requestedUser.id} selectedstatus={eachFriend.status} firstname={eachFriend.requestedUser.firstname} lastname={eachFriend.requestedUser.lastname} selecteduserId={userId}/>)) : <div>No data</div>}
                    </div>
                </div>

                <div className='container friend-wrapper mt-5 mb-5'>
                    <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Pending</h2>
                    <div className="d-grid gap-5 justify-content-center" style={{gridTemplateColumns:"repeat(auto-fill, 15rem)"}}>
                        {friendList ? friendList.filter(eachFriend => eachFriend.status === 'pending').map((eachFriend) => ( 
                            <FriendRequests key={eachFriend.id} friendId = {eachFriend.id} selectedrequestedUser={eachFriend.requestedUser.id} selectedstatus={eachFriend.status} firstname={eachFriend.requestedUser.firstname} lastname={eachFriend.requestedUser.lastname} selecteduserId={userId}/>)) : <div>No data</div>}
                     
                    </div>
                </div> */}
                <FriendRequestComp compStatus={'Pending request'} />

                {/* <div className='container friend-wrapper mt-5 mb-5'>
                    <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Friends</h2>
                    <div className="d-grid gap-5 justify-content-center" style={{gridTemplateColumns:"repeat(auto-fill, 15rem)"}}>
                        {friendList ? friendList.filter(eachFriend => eachFriend.status === 'accepted').map((eachFriend) => ( 
                            <FriendRequests key={eachFriend.id} friendId = {eachFriend.id} selectedrequestedUser={eachFriend.requestedUser.id} selectedstatus={eachFriend.status} firstname={eachFriend.requestedUser.firstname} lastname={eachFriend.requestedUser.lastname} selecteduserId={userId}/>)) : <div>No data</div>}
                    </div>
                </div> */}

                <FriendListComp compStatus={'Accepted request'} />
                <br/> <br/>
            </div>
            <NavBar />
       
        </div>
    );
};

export default TestFriends;