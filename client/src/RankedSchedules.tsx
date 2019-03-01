import * as React from 'react';
import './App.css';

import logo from './logo.svg';

interface CourseSection {
    courseID: string;
    title: string;
    scheduleNum: string;
    units: string;
    seats: string;
    days: Array<string>;
    times: Array<string>;
    locations: Array<string>;
    instructors: Array<string>;
    parentCourse: CourseSection;
}

interface Schedule {
    coursesInSchedule: Array<CourseSection>;
}

export interface RankedSchedules {
    id: number;
    resultCode: string;
    completionTime: string;
    numValidSchedules: number;
    numInvalidSchedules: number
    numPerformedPermutations: number
    numTheoreticalPermutations: number;
    userCourseSelection: Array<string>;
    topRankedValidSchedules: Array<Schedule>;
    coursesWithAllSectionsFull: Array<string>;
    coursesWithNoSectionsOffered: Array<string>;
}

interface RankedSchedulesProps {
}

interface RankedSchedulesState {
    schedules: Array<RankedSchedules>;
    isLoading: boolean;
}

class RankedSchedules extends React.Component<RankedSchedulesProps, RankedSchedulesState> {

    constructor(props: RankedSchedulesProps) {
        super(props);

        this.state = {
            schedules: [],
            isLoading: false
        };
    }

    componentDidMount() {
        this.setState({isLoading: true});

        fetch('http://localhost:8080/v1/test')
            .then(response => response.json())
            .then(data => this.setState({schedules: data, isLoading: false}));
    }

    render() {
        const {schedules, isLoading} = this.state;

        if(isLoading) {
            return <p>Loading...</p>;
        }

        return (
            <div>
                <h2>Ranked Schedules List</h2>
                {schedules.map((schedule: RankedSchedules) =>
                    <div key={schedule.id}>
                        {schedule.resultCode}
                    </div>
                )}
            </div>
        );
    }
}

export default RankedSchedules;
